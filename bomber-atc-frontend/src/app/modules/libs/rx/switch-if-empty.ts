import {empty, Observable, ObservableInput, OperatorFunction} from "rxjs";
import {flatMap, isEmpty} from "rxjs/operators";
import {of} from "rxjs/internal/observable/of";

export function switchIfEmpty<T, R>(ifEmpty: ObservableInput<R>): OperatorFunction<T, T | R> {
  return (source: Observable<T | R>) => {
    return source.pipe(
        isEmpty(),
        flatMap(e => e ? ifEmpty : source)
    ) as Observable<T | R>
  }
}


export function testSwitchIfEmpty() {

  const a = of('A');
  const b = of('B', 'C', 'D').pipe(
      // tap(val => console.log(`the B val`))
  );

  const e = empty();

  e
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
