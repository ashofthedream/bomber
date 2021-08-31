import { UserAction, UserActions } from './user.actions';
import { initialUserState, UserState } from './user.state';


export const userReducers = (state = initialUserState, action: UserActions): UserState => {
  switch (action.type) {
    case UserAction.GetUserSuccess:
      return {
        ...state,
        user: action.user
      };

    default:
      return state;
  }
};


