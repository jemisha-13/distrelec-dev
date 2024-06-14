import { Observable, Subject } from 'rxjs';

export class PendingRequest<T> {
  observable: Observable<T>;
  subscription: Subject<T>;

  constructor(observable: Observable<T>, subscription: Subject<T>) {
    this.observable = observable;
    this.subscription = subscription;
  }
}

export class ObservableQueue<T> {
  private requestQueue: PendingRequest<T>[] = [];

  // We should only use this for requests that are expected to complete, in order to avoid memory leaks
  queue(observable: Observable<T>) {
    return this.addRequestToQueue(observable);
  }

  private execute(requestData) {
    const { observable, subscription } = requestData;
    observable.subscribe({
      next: (res) => {
        subscription.next(res);
        this.requestQueue.shift();
        this.startNextRequest();
      },
      complete: () => {
        subscription.complete();
      },
      error: (err) => {
        subscription.next(err);
        this.requestQueue.shift();
        this.startNextRequest();
      },
    });
  }

  private addRequestToQueue(observable: Observable<T>) {
    const sub = new Subject<T>();
    const request = new PendingRequest(observable, sub);

    this.requestQueue.push(request);
    if (this.requestQueue.length === 1) {
      this.startNextRequest();
    }
    return sub;
  }

  private startNextRequest() {
    if (this.requestQueue.length > 0) {
      this.execute(this.requestQueue[0]);
    }
  }
}
