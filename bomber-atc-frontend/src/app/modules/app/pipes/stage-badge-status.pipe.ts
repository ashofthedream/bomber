import { Pipe, PipeTransform } from '@angular/core';
import { Stage } from '../models/application-state';

@Pipe({
  name: 'stageBadgeStatus',
})
export class StageBadgeStatusPipe implements PipeTransform {
  transform(stage: Stage) {
    switch (stage) {
      case Stage.IDLE: return 'default';
      case Stage.WARM_UP: return 'warning';
      case Stage.TEST: return 'error';
    }

    return 'default';
  }
}
