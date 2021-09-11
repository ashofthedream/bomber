import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AtcState } from '../../../shared/store/atc.state';
import { activeFlightHistogram } from '../../store/flight.selectors';

@Component({
  selector: 'atc-flight-graph',
  templateUrl: './flight-graph.component.html'
})
export class FlightGraphComponent implements OnInit {
  histogram = this.store.select(activeFlightHistogram);

  options: any;
  updateOptions: any;

  constructor(private readonly store: Store<AtcState>) {
  }

  ngOnInit(): void {

    // initialize chart options:
    this.options = {
      legend: {
        data: [
          // 'min',
          'mean',
          // '0.75000p',
          // '0.90000p',
          '0.95000p',
          // '0.99000p',
          // '0.99900p',
          'max'
        ],
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
        type: 'time',
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
        // {
        //   name: 'min',
        //   type: 'line',
        //   showSymbol: false,
        //   hoverAnimation: false,
        //   data: []
        // },
        {
          name: 'mean',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        // {
        //   name: '0.75000p',
        //   type: 'line',
        //   showSymbol: false,
        //   hoverAnimation: false,
        //   data: []
        // },
        // {
        //   name: '0.90000p',
        //   type: 'line',
        //   showSymbol: false,
        //   hoverAnimation: false,
        //   data: []
        // },
        {
          name: '0.95000p',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        },
        // {
        //   name: '0.99000p',
        //   type: 'line',
        //   showSymbol: false,
        //   hoverAnimation: false,
        //   data: []
        // },
        // {
        //   name: '0.99900p',
        //   type: 'line',
        //   showSymbol: false,
        //   hoverAnimation: false,
        //   data: []
        // },
        {
          name: 'max',
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: []
        }]
    };


    this.histogram
        .subscribe(points => {
          // ['min', 'mean', '0.75000p', '0.90000p', '0.95000p', '0.99900p', 'max'],
          let mean = points.map(point => {

            return {
              value: [point.timestamp, point.percentiles[1]]
            };
          });

          let p95 = points.map(point => {

            return {
              value: [point.timestamp, point.percentiles[4]]
            };
          });

          let max = points.map(point => {

            return {
              value: [point.timestamp, point.percentiles[7]]
            };
          });

          this.updateOptions = {
            series: [
              // { data: [] },
              { data: mean },
              // { data: [] },
              // { data: [] },
              { data: p95 },
              // { data: [] },
              // { data: [] },
              { data: max }
            ]
          };
        });
  }
}
