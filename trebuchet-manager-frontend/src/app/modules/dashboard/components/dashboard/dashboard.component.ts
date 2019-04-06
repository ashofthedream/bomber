import {Component} from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {

  constructor() {
    let ws = new WebSocket('ws://localhost:8088/socket');
    ws.onmessage = ev => {
      console.log(ev)
    }
  }
}
