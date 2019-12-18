import {Component, OnInit} from '@angular/core';
import {of} from "rxjs/internal/observable/of";
import {flatMap, isEmpty} from "rxjs/operators";
import {empty, Observable, ObservableInput, OperatorFunction} from "rxjs";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  ngOnInit() {

    const a = of('A');
    const b = of('B', 'C', 'D').pipe(
        // tap(val => console.log(`the B val`))
    );
    const e = empty();

    of('some string')
        .pipe(
            flatMap(key => e),
            switchIfEmpty(b)
        )
        .subscribe(
            value => console.log(value),
            e => console.log('err', e),
            () => console.log('fucking end')
        );
  }

}

export function switchIfEmpty<T, R>(ifEmpty: ObservableInput<R>): OperatorFunction<T, T | R> {
  return (source: Observable<T | R>) => {
    return source.pipe(
        isEmpty(),
        flatMap(e => e ? ifEmpty : source)
    ) as Observable<T | R>
  }
}
