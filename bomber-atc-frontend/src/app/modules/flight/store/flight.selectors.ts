import { createSelector } from '@ngrx/store';
import { applications } from '../../app/store/app.selectors';
import { AppTreeNode } from '../../app/store/app.state';
import { AtcState } from '../../shared/store/atc.state';
import { TestFlight } from '../models/test-flight';

const flightsState = (state: AtcState) => state.flights;

export const activeFlight = createSelector(flightsState, state => state.active);
export const activeFlightLog = createSelector(activeFlight, flight => flight.events);
export const hasActiveFlight = createSelector(activeFlight, flight => !!flight);
export const hasNoActiveFlight = createSelector(hasActiveFlight, has => !has);

export const activeFlightHistogram = createSelector(flightsState, state => state.histogram);

export const flightAll = createSelector(applications, (apps: AppTreeNode[]): TestFlight => {

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


export const createPlanState = createSelector(flightsState, state => state.createPlan);
export const currentPlan = createSelector(createPlanState, state => state.plan);
export const currentTestApps = createSelector(currentPlan, plan => plan.testApps);
