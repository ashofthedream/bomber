import { createSelector } from '@ngrx/store';
import { AtcState } from './atc.state';

export const usersState = (state: AtcState) => state.users;

export const currentUser = createSelector(usersState, state => state.user);
