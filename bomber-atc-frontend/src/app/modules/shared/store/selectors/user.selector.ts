import { AtcState } from "../state/atc.state";
import { createSelector } from "@ngrx/store";
import { UserState } from "../state/user.state";

export const selectUsersState = (state: AtcState) => state.users;

export const currentUser = createSelector(selectUsersState, (state: UserState) => state.user);