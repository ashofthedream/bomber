import { Component, Input } from '@angular/core';
import { TestApp } from '../../models/test-app';

@Component({
  selector: 'atc-app-label',
  templateUrl: './app-label.component.html'
})
export class AppLabelComponent {

  @Input()
  app: TestApp;

}
