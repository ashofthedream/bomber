import { Flight, FlightProgress, HistogramPoint } from '../models/flight';
import { FlightAction, FlightActions } from './flight.actions';
import { FlightState, initialFlightState } from './flight.state';

export const flightReducers = (state = initialFlightState, action: FlightActions): FlightState => {
  switch (action.type) {
    case FlightAction.GetActiveFlightSuccess:
      let histogram: HistogramPoint[] = [];
      let progress: FlightProgress = null;

      if (action.flight) {
        //   console.log(action.flight.data);
        Object.values(action.flight.histogram)
            .forEach(byCarrier => histogram = Object.values(byCarrier));

        Object.values(action.flight.progress)
            .forEach(byCarrier => progress = byCarrier);
      }

      return {
        ...state,
        active: action.flight,
        histogram,
        progress
      };

    case FlightAction.ActiveFlightUpdated:
      return {
        ...state,
        active: {
          plan: action.plan,
          events: [],
          progress: null,
          histogram: []
        }
      };


    case FlightAction.FlightProgressUpdated:
      const newProgressFlight: Flight = {
        plan: {
          id: - 1,
          testApps: []
        },
        histogram: [],
        progress: null,
        events: []
      };

      return {
        ...state,
        active: state.active ? state.active : newProgressFlight,
        progress: action.progress
      };


    case FlightAction.FlightHistogramUpdated:
      const newHistogramFlight: Flight = {
        plan: {
          id: - 1,
          testApps: []
        },
        histogram: [],
        progress: null,
        events: []
      };

      return {
        ...state,
        active: state.active ? state.active : newHistogramFlight,
        histogram: action.points
      };

    case FlightAction.FlightLogUpdated:
      const newLogFlight: Flight = {
        plan: {
          id: - 1,
          testApps: []
        },
        histogram: [],
        progress: {},
        events: [action.event]
      };

      return {
        ...state,
        active: state.active ? { ...state.active, events: [...state.active.events, action.event] } : newHistogramFlight,
      };


    case FlightAction.GetAllFlightsSuccess:
      return {
        ...state,
        all: action.flights
      };

    case FlightAction.AddToFlight:
      let plan = state.createPlan.plan;

      const otherApps = plan.testApps.filter(testApp => testApp.name !== action.testCase.testApp);
      let foundApp = plan.testApps.find(testApp => testApp.name === action.testCase.testApp);
      if (!foundApp) {
        foundApp = {
          name: action.testCase.testApp,
          testSuites: []
        };
      }

      const otherTestSuites = foundApp.testSuites.filter(testSuite => testSuite.name !== action.testCase.testSuite);
      let foundTestSuite = foundApp.testSuites.find(testSuite => testSuite.name === action.testCase.testSuite);
      if (!foundTestSuite) {
        foundTestSuite = {
          name: action.testCase.testSuite,
          testCases: []
        };
      }

      foundTestSuite = {
        ...foundTestSuite,
        testCases: [...foundTestSuite.testCases, { name: action.testCase.testCase, configuration: null }]
      };

      foundApp = {
        ...foundApp,
        testSuites: [...otherTestSuites, foundTestSuite]
      };

      return {
        ...state,
        createPlan: {
          ...state.createPlan,
          plan: {
            ...plan,
            testApps: [...otherApps, foundApp]
          },
        }
      };

    case FlightAction.CancelCreatedFlight:
      return {
        ...state,
        createPlan: {
          ...state.createPlan,
          plan: {
            testApps: []
          },
        }
      };

    default:
      return state;
  }
};
