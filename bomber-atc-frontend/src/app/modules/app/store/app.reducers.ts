import { AppAction, AppActions } from './app.actions';
import { AppState, initialAppState } from './app.state';


export const appReducers = (state = initialAppState, action: AppActions): AppState => {
  switch (action.type) {
    case AppAction.Select:
      return {
        ...state
      };

    default:
      return state;
  }
};
