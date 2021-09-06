import { FlightData } from '../models/flight-data';
import { FlightRecord } from '../models/flight-record';
import { FlightAction, FlightActions } from './flight.actions';
import { FlightState, initialFlightState } from './flight.state';

export const flightReducers = (state = initialFlightState, action: FlightActions): FlightState => {
  switch (action.type) {
    case FlightAction.GetActiveFlightSuccess:

      let onlyHistograms = [];
      console.log(action.flight.data);
      Object.values(action.flight.data)
          .forEach((data: FlightData) => {

            const hrec = data.records
                .filter(record => record.type === 'TEST_CASE_HISTOGRAM');

            let records: FlightRecord[] = [];
            for (let i = 0; i < hrec.length; i ++) {
              const record = hrec[i];
              if (records.length === 0) {
                records.push(record);
                continue;
              }

              let last = records[records.length - 1];
              if (last.testSuite === record.testSuite && last.testCase === record.testCase) {
                records[records.length - 1] = record;
              }
              else {
                records.push(record);
              }
            }

            records.forEach(record =>  {
                record.histograms.forEach(h => {
                  onlyHistograms.push({
                    testSuite: record.testSuite,
                    testCase: record.testCase,
                    timestamp: h.timestamp,
                    label: h.label,
                    values: h.percentiles
                  });
                });
            });

            console.log(onlyHistograms);
          });

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

    default:
      return state;
  }
};
