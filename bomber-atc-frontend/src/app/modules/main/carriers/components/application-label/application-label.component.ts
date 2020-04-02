import {Component, Input, OnInit} from '@angular/core';
import {Application} from "../../../model/application";
import {NzBadgeStatusType} from "ng-zorro-antd/badge/nz-badge.component";
import {Stage} from "../../../model/application-state";

@Component({
  selector: 'carriers-app-label',
  templateUrl: './application-label.component.html'
})
export class ApplicationLabelComponent {

  @Input()
  app: Application;


  getStatus(): NzBadgeStatusType {
    switch (this.app.state.stage) {
      case Stage.Idle: return "default";
      case Stage.WarmUp: return "warning";
      case Stage.Test: return "error";
    }
  }

}
