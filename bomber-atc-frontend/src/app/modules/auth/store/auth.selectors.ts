import { createSelector } from '@ngrx/store';
import { usersState } from '../../shared/store/user.selectors';
import { UserState } from '../../shared/store/user.state';

export const isAuthenticated = createSelector(usersState, (state: UserState) => !!state.user);
