import {Component, OnInit} from '@angular/core';
import {Carrier} from "../../model/carrier";
import {ApplicationService} from "../../services/application.service";

@Component({
  selector: 'carrier-active-page',
  templateUrl: './active-carriers-page.component.html'
})
export class ActiveCarriersPageComponent implements OnInit {
  carriers: Carrier[] = [];

  constructor(private readonly service: ApplicationService) {
  }

  ngOnInit(): void {
    this.service.getActiveCarriers()
        .subscribe(instances => this.carriers = instances);
  }

  startAll() {
    this.service.startAll()
        .subscribe();
  }

  start(carrier: Carrier) {
    this.service.startApp(carrier, carrier.app)
        .subscribe();
  }


  stopAll() {
    this.service.stopAllApps()
        .subscribe();
  }

  stop(carrier: Carrier) {
    this.service.stopApp(carrier, carrier.app)
        .subscribe();
  }
}
