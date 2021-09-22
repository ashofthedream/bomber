import { createSelector } from '@ngrx/store';
import { applications } from '../../app/store/app.selectors';
import { AppTreeNode } from '../../app/store/app.state';
import { AtcState } from '../../shared/store/atc.state';
import { TestFlight } from '../models/test-flight';

const flightsState = (state: AtcState) => state.flights;

export const activeFlight = createSelector(flightsState, state => state.active);
export const activeFlightLog = createSelector(activeFlight, flight => flight?.events ? flight.events : []);
export const hasActiveFlight = createSelector(activeFlight, flight => !!flight);
export const hasNoActiveFlight = createSelector(hasActiveFlight, has => !has);

export const activeFlightHistogram = createSelector(flightsState, state => state.histogram);
export const activeFlightProgress = createSelector(flightsState, state => state.progress);


export const createPlanState = createSelector(flightsState, state => state.createPlan);

export const createdFlightPlan = createSelector(createPlanState, state => state.plan);
export const defaultFlightPlan = createSelector(applications, (apps: AppTreeNode[]): TestFlight => {
  return {
    testApps: apps.map(app => {
      return {
        name: app.key,
        testSuites: app.children.map(testSuite => {
          return {
            name: testSuite.key,
            testCases: testSuite.children.map(testCase => {
              return {
                name: testCase.key,
                configuration: null
              };
            })
          };
        })
      };
    })
  };
});

export const createdFlightPlanApps = createSelector(createdFlightPlan, plan => plan.testApps);
export const flightCanBeStarted = createSelector(createPlanState, state => state.plan.testApps.length > 0);
export const flightCanNotBeStarted = createSelector(flightCanBeStarted, state => !state);