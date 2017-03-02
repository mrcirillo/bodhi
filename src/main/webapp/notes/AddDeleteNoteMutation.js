/**
 * Created by mcirillo on 2/4/17.
 */

import Relay, { Mutation } from 'react-relay';

export default class AddDeleteNoteMutation extends Mutation {

	static ADD = "addNote";
	static DELETE = "deleteNote";

	static fragments = {
		lastSelectedRoot: () => Relay.QL`fragment on NoteRoot { id }`
	};

	getMutation() {
		switch (this.props.type) {
			case AddDeleteNoteMutation.ADD:
				return Relay.QL`mutation { addNote }`;
				break;

			case AddDeleteNoteMutation.DELETE:
				return Relay.QL`mutation { deleteNote }`;
				break;

			default:
				throw new Exception("Invalid mutation type");
		}
	}

	getVariables() {
		return {
			leftBound: this.props.leftBound
		};
	}

	getConfigs() {
		return [
			{
				type: 'FIELDS_CHANGE',
				fieldIDs: {
					lastSelectedRoot: this.props.lastSelectedRoot.id
				}
			}
		];
	}

	getFatQuery() {
		return Relay.QL`
		fragment on AddDeleteNotePayload {
			lastSelectedRoot {
				id,
				lastEditedNode {
					id,
					content(preview: false)
				},
				nodes {
					leftBound, rightBound
				}
			}
		}`;
	}
}