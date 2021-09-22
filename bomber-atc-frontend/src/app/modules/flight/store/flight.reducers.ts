import { HistogramPoint } from '../models/flight-record';
import { FlightAction, FlightActions } from './flight.actions';
import { FlightState, initialFlightState } from './flight.state';

export const flightReducers = (state = initialFlightState, action: FlightActions): FlightState => {
  switch (action.type) {
    case FlightAction.GetActiveFlightSuccess:

      let onlyHistograms: HistogramPoint[] = [];
      if (action.flight) {
        //   console.log(action.flight.data);
        Object.values(action.flight.histogram)
            .forEach((byTime: Map<number, HistogramPoint[]>) => {

              Object.values(byTime)
                  .forEach(value => {
                    onlyHistograms.push(value);
                  });
            });
      }

      return {
        ...state,
        active: action.flight,
        histogram: onlyHistograms
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
