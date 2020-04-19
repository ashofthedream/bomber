import {Component, Input} from '@angular/core';
import {Application} from "../../../main/models/application";
import {Stage} from "../../../main/models/application-state";
import {NzBadgeStatusType} from "ng-zorro-antd/badge/types";

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
