import { initialUserState, UserState } from "../state/user.state";
import { UserAction, UserActions } from "../actions/user.actions";


export const userReducers = (state = initialUserState, action: UserActions): UserState => {
  switch (action.type) {
    case UserAction.GetUserSuccess:
      return {...state, user: action.user}

    default:
      return state
  }
}


