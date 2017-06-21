/**
 * This file was generated by:
 *   relay-compiler
 *
 * @providesModule NavBar_user.graphql
 * @generated SignedSource<<bbe7b8690e7d2d7b7f4698d802a01ca4>>
 * @flow
 * @nogrep
 */

/* eslint-disable */

'use strict';

/*::
import type {ConcreteFragment} from 'relay-runtime';
export type NavBar_user = {|
  +username: ?string;
|};
*/


const fragment /*: ConcreteFragment*/ = {
  "argumentDefinitions": [],
  "kind": "Fragment",
  "metadata": null,
  "name": "NavBar_user",
  "selections": [
    {
      "kind": "ScalarField",
      "alias": null,
      "args": null,
      "name": "username",
      "storageKey": null
    },
    {
      "kind": "FragmentSpread",
      "name": "NoteRootPicker_user",
      "args": null
    },
    {
      "kind": "FragmentSpread",
      "name": "UserMenu_user",
      "args": null
    }
  ],
  "type": "User"
};

module.exports = fragment;
