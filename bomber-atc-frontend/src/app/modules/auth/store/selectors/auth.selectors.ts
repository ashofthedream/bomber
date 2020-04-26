import { createSelector } from "@ngrx/store";
import { UserState } from "../../../shared/store/state/user.state";
import { selectUsersState } from "../../../shared/store/selectors/user.selector";

export const isAuthenticated = createSelector(selectUsersState, (state: UserState) => !!state.user);