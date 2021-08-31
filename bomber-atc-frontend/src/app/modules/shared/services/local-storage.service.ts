import { Injectable } from '@angular/core';
import { empty, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  public save(key: string, value: any) {
    console.log(`storage.save: ${key}`, value);
    localStorage.setItem(key, JSON.stringify(value));
  }

  public load<T>(key: string): Observable<T> {
    console.log(`storage.load: ${key}`);
    const item = localStorage.getItem(key);
    if (!item) {
      return empty();
    }

    return of(JSON.parse(item));
  }

  public remove(key: string) {
    console.log(`storage.remove: ${key}`);
    localStorage.removeItem(key);
  }
}
