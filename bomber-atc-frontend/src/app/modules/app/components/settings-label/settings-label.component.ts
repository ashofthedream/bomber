import { Component, Input } from '@angular/core';
import { Settings } from '../../models/settings';

@Component({
  selector: 'atc-settings-label',
  templateUrl: './settings-label.component.html'
})
export class SettingsLabelComponent {

  @Input()
  title: string;

  @Input()
  settings: Settings;
}
