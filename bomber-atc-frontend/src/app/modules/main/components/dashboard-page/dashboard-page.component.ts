import {Component, OnDestroy, OnInit} from '@angular/core';
import {interval, Subscription, timer} from "rxjs";
import {ApplicationService} from "../../services/application.service";
import {Instance} from "../../model/instance";

@Component({
  selector: 'atc-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.css']
})
export class DashboardPageComponent implements OnInit, OnDestroy {

  private activeAppsSub: Subscription;
  instances: Instance[] = [];

  constructor(private readonly service: ApplicationService) {
  }

  ngOnInit() {
    this.activeAppsSub = timer(0, 3000)
        .subscribe(() => {
          this.service.getActiveApplications()
              .subscribe(instances => this.instances = instances);
        });
  }

  ngOnDestroy(): void {
    if (this.activeAppsSub)
      this.activeAppsSub.unsubscribe();
  }
}
