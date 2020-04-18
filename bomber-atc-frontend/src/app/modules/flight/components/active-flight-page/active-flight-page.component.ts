import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription, timer} from "rxjs";
import {flatMap} from "rxjs/operators";
import {FlightService} from "../../services/flight.service";
import {Flight} from "../../models/flight";
import {FlightData} from "../../models/flight-data";
import {ApplicationState} from "../../../main/models/application-state";

@Component({
  selector: 'flights-active-page',
  templateUrl: './active-flight-page.component.html'
})
export class ActiveFlightPageComponent implements OnInit, OnDestroy {
  flight: Flight;

  private flightSub: Subscription;

  constructor(private readonly service: FlightService) {
  }

  ngOnInit() {
    this.flightSub = timer(0, 3000)
        .pipe(
            flatMap(() => this.service.getActive())
        )
        .subscribe(flight => {
          this.flight = flight;
        });
  }

  ngOnDestroy(): void {
    if (this.flightSub)
      this.flightSub.unsubscribe();
  }

  flightData(): FlightData[] {
    if (this.flight)
      return Object.values(this.flight.data);

    return [];
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
