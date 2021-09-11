import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ApplicationState } from '../../../app/models/application-state';
import { AtcState } from '../../../shared/store/atc.state';
import { activeFlight, activeFlightHistogram } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flights-active',
  templateUrl: './active-flight-page.component.html'
})
export class ActiveFlightPageComponent implements OnInit {
  flight = this.store.select(activeFlight);

  histogram = this.store.select(activeFlightHistogram);

  options: any;
  updateOptions: any;

  private value: number;
  private p050data = [];
  private p095data = [];
  private p100data = [];
  private timer: any;
  private count = 0;

  constructor(private readonly store: Store<AtcState>) {
  }

  ngOnInit(): void {
    this.value = Math.random() * 1000;

    for (let i = 0; i < 100; i ++) {
      // let data = this.randomData();
      // this.p050data.push(data[0]);
      // this.p095data.push(data[1]);
      // this.p100data.push(data[2]);
    }

    // initialize chart options:
    this.options = {
      legend: {
        data: ['min', 'mean', '0.75000p', '0.90000p', '0.95000p', '0.99000p', '0.99900p', 'max'],
        align: 'left',
      },
      // title: {
      // text: 'Time Percentiles'
      // },
      // tooltip: {
      //   trigger: 'axis',
      // formatter: (params) => {
      //   params = params[0];
      //   const date = new Date(params.name);
      //   return date.toString();
      // },
      // axisPointer: {
      //   animation: false
      // }
      // },
      xAxis: {
        type: 'value',
        // splitLine: {
        //   show: true
        // }
      },
      yAxis: {
        type: 'value',
        // boundaryGap: [0, '100%'],
        // splitLine: {
        //   show: false
        // }
      },
      series: [
        {
          name: 'min',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: 'mean',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: '0.75000p',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: '0.90000p',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: '0.95000p',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: '0.99000p',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: '0.99900p',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        {
          name: 'max',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: this.p100data
        }]
    };


    this.histogram
        .subscribe(points => {
          // ['min', 'mean', '0.75000p', '0.90000p', '0.95000p', '0.99900p', 'max'],
          let mean = points.map((point, idx) => {

            return {
              value: [idx, point.values[1]]
            };
          });

          let max = points.map((point, idx) => {

            return {
              value: [idx, point.values[7]]
            };
          });

          this.updateOptions = {
            series: [
              { data: [] },
              { data: mean },
              { data: [] },
              { data: [] },
              { data: [] },
              { data: [] },
              { data: [] },
              { data: max }
            ]
          };
        });

    // Mock dynamic data:
    // this.timer = setInterval(() => {
    //   const data = this.randomData();
    //   this.p050data.push(data[0]);
    //   this.p095data.push(data[1]);
    //   this.p100data.push(data[2]);
    //   // ['min', 'mean', '0.75000p', '0.90000p', '0.95000p', '0.99900p', 'max'],
    //
    //   // update series data:
    //   this.updateOptions = {
    //     series: [
    //         { data: [] },
    //         { data: this.p050data },
    //         { data: [] },
    //         { data: [] },
    //         { data: this.p095data },
    //         { data: [] },
    //         { data: this.p100data }
    //     ]
    //   };
    //
    // }, 1000);
  }

  randomData() {
    const now = new Date();
    this.count ++;
    this.value = this.value + Math.random() * 21 - 10;
    const mean = Math.round(this.value);
    const p75 = Math.round(mean * ( 1 + Math.random() * 0.05 ));
    const p95 = Math.round(mean * ( 1 + Math.random() * 0.05 ));
    const max = Math.round(p95 * ( 1 + Math.random() * 0.07 ));
    // return [mean, p95 + 100, max + 100];
    return [{
        // name: now.toString(),
        value: [this.count - 1, mean]
      },
      {
        // name: now.toString(),
        value: [this.count - 1, p95]
      },
      {
        // name: now.toString(),
        value: [this.count - 1, max]
      }];
  }

  iterationsProgress(state: ApplicationState): number {
    return Math.round(this.currentIterations(state) / state.settings.totalIterationsCount * 100);
  }

  timeProgress(state: ApplicationState): number {
    return Math.round(this.currentTime(state) / state.settings.duration * 100);
  }

  currentTime(state: ApplicationState): number {
    return state.settings.duration - state.remainTime;
  }

  currentIterations(state: ApplicationState): number {
    return state.settings.totalIterationsCount - state.remainTotalIterations;
  }
}
