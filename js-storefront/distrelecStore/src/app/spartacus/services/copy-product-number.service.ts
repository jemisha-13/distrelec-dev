import { Injectable, NgZone } from '@angular/core';
import { ArticleNumberPipe } from '@pipes/article-number.pipe';
import { ClipboardService } from 'ngx-clipboard';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CopyProductNumberService {
  private copiedState_: BehaviorSubject<{ [key: string]: boolean }> = new BehaviorSubject({});
  private articleNumberPipe: ArticleNumberPipe = new ArticleNumberPipe();
  private timeoutId: ReturnType<typeof setTimeout> | undefined;

  constructor(private clipboardApi: ClipboardService, private ngZone: NgZone) {}

  copyNumber(copiedText: string, key: string, origin?: string): void {
    this.clipboardApi.copyFromContent(this.articleNumberPipe.transform(copiedText, origin));

    const copiedState = { [key]: true };
    this.copiedState_.next(copiedState);
    this.ngZone.run(() => {
      if (this.timeoutId) {
        clearTimeout(this.timeoutId);
      }
      this.timeoutId = setTimeout(() => {
        const resetState = { [key]: false };
        this.copiedState_.next(resetState);
      }, 2000);
    });
  }

  get copiedState$(): Observable<{ [key: string]: boolean }> {
    return this.copiedState_.asObservable();
  }
}
