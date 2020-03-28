import {Component, OnInit} from '@angular/core';
import {Instance} from "../../model/instance";
import {ApplicationService} from "../../services/application.service";

@Component({
  selector: 'atc-active-instances-page',
  templateUrl: './active-instances-page.component.html'
})
export class ActiveInstancesPageComponent implements OnInit {
  instances: Instance[] = [];

  constructor(private readonly service: ApplicationService) {
  }

  ngOnInit(): void {
    this.service.getActiveApplications()
        .subscribe(instances => this.instances = instances);
  }

  start(instance: Instance) {
    this.service.startApp(instance)
        .subscribe();
  }

  shutdown(instance: Instance) {
    this.service.shutdownApp(instance)
        .subscribe();
  }
}
