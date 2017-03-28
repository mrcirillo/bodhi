package bodhi.schema

import bodhi.GraphQLHelpers
import bodhi.User
import gql.DSL
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import graphql.Scalars
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import org.hibernate.FetchMode

/**
 * bodhi
 * @author mcirillo
 */
trait Root {

	final static GraphQLObjectType root = DSL.type('Root') {

		field 'nodes', new GraphQLList(Schema.node)
		field 'name', GraphQLString
		field 'description', GraphQLString
		field 'lastEditedNode', Schema.node

		field 'lastUpdated', {
			type Scalars.GraphQLLong
			fetcher { env ->
				(env.source as bodhi.Root).lastUpdated.time
			}
		}

		addInterface Schema.nodeInterface
	}

	final static GraphQLFieldDefinition addNote = DSL.field('addNote') {

		argument 'input', DSL.input('AddNoteInput') {
			field 'clientMutationId', GraphQLString
			field 'leftBound', new GraphQLNonNull(GraphQLInt)
		}

		type DSL.type('AddDeleteNotePayload') {
			field 'clientMutationId', GraphQLString
			field 'lastSelectedRoot', root
		}

		fetcher { env ->
			def sss = Holders.applicationContext.getBean('springSecurityService') as SpringSecurityService

			def lsr = bodhi.Root.withCriteria {
				idEq(sss.currentUser.lastSelectedRoot.id)
				fetchMode 'nodes', FetchMode.SELECT
			}.first() as bodhi.Root

			def leftBound = env.arguments.input.leftBound as int
			lsr.addNoteHere(leftBound)

			[
					lastSelectedRoot: lsr,
					clientMutationId: env.arguments.input.clientMutationId
			]
		}
	}

	final static GraphQLFieldDefinition deleteNote = DSL.field("deleteNote") {

		argument 'input', DSL.input('DeleteNoteInput') {
			field 'clientMutationId', GraphQLString
			field 'nodeId', new GraphQLNonNull(GraphQLID)
		}

		type new GraphQLTypeReference('AddDeleteNotePayload')

		fetcher { env ->
			def id = GraphQLHelpers.fromGlobalId(env.arguments.input.nodeId as String).id as long

			def sss = Holders.applicationContext.getBean('springSecurityService') as SpringSecurityService
			def lsr = (sss.currentUser as User).lastSelectedRoot
			lsr.deleteNoteFromHere(id)

			return [
					lastSelectedRoot: lsr,
					clientMutationId: env.arguments.input.clientMutationId
			]
		}
	}

	final static GraphQLEnumType gqlMoveMode = DSL.enum('MoveMode') {
		value 'Before', bodhi.Root.MoveMode.Before
		value 'After', bodhi.Root.MoveMode.After
	}

	final static GraphQLFieldDefinition moveNote = DSL.field('moveNote') {

		argument 'input', DSL.input('MoveNoteInput') {
			field 'clientMutationId', GraphQLString
			field 'movingNodeId', new GraphQLNonNull(GraphQLID)
			field 'targetNodeId', new GraphQLNonNull(GraphQLID)
			field 'moveMode', new GraphQLNonNull(gqlMoveMode)
		}

		type DSL.type('MoveNotePayload') {
			field 'clientMutationId', GraphQLString
			field 'lastSelectedRoot', root
		}

		fetcher { env ->
			def sss = Holders.applicationContext.getBean('springSecurityService') as SpringSecurityService

			def lsr = bodhi.Root.withCriteria {
				idEq(sss.currentUser.lastSelectedRoot.id)
				fetchMode 'nodes', FetchMode.SELECT
			}.first() as bodhi.Root

			def movingNodeId = RelayHelpers.fromGlobalId(env.arguments.input.movingNodeId as String).id as long
			def targetNodeId = RelayHelpers.fromGlobalId(env.arguments.input.targetNodeId as String).id as long
			def moveMode = env.arguments.input.moveMode as bodhi.Root.MoveMode

			lsr.moveNote movingNodeId, targetNodeId, moveMode

			[
			        lastSelectedRoot: lsr,
					clientMutationId: env.arguments.input.clientMutationId
			]
		}
	}
}
