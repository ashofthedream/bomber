import {Component, OnInit} from '@angular/core';
import {Carrier} from "../../../model/carrier";
import {CarrierService} from "../../../services/carrier.service";
import {FlightService} from "../../../services/flight.service";

@Component({
  selector: 'carriers-active-page',
  templateUrl: './active-carriers-page.component.html'
})
export class ActiveCarriersPageComponent implements OnInit {
  carriers: Carrier[] = [];

  constructor(private readonly carrierService: CarrierService,
              private readonly flightService: FlightService) {
  }

  ngOnInit(): void {
    this.carrierService.getActiveCarriers()
        .subscribe(instances => this.carriers = instances);
  }

  startAll() {
    this.flightService.startAll()
        .subscribe();
  }

  start(carrier: Carrier) {
    this.flightService.start(carrier, carrier.app)
        .subscribe();
  }


  stopAll() {
    this.flightService.stopAll()
        .subscribe();
  }

  stop(carrier: Carrier) {
    this.flightService.stop(carrier, carrier.app)
        .subscribe();
  }
}
