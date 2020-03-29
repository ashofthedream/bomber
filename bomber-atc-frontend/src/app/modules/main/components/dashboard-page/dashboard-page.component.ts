import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription, timer} from "rxjs";
import {ApplicationService} from "../../services/application.service";
import {Carrier} from "../../model/carrier";

@Component({
  selector: 'atc-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.css']
})
export class DashboardPageComponent implements OnInit, OnDestroy {

  private activeAppsSub: Subscription;
  carriers: Carrier[] = [];

  constructor(private readonly service: ApplicationService) {
  }

  ngOnInit() {
    this.activeAppsSub = timer(0, 3000)
        .subscribe(() => {
          this.service.getActiveCarriers()
              .subscribe(carriers => this.carriers = carriers);
        });
  }

  ngOnDestroy(): void {
    if (this.activeAppsSub)
      this.activeAppsSub.unsubscribe();
  }
}
