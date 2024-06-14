import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-reference-input',
  templateUrl: './reference-input.component.html',
  styleUrls: ['./reference-input.component.scss'],
})
export class ReferenceInputComponent implements OnInit {
  @Output() onPaste = new EventEmitter<string>();
  @Input() className: string;
  @Input() id: string;
  @Input() value: string;
  @Input() maxLength = 35;
  @Input() control: FormControl;
  @Input() type: 'payment' | 'cart' = 'cart';
  @Input() labelText: string;
  @Input() labelInfoText: string;
  @Input() bottomInfoText: string;
  @ViewChild('refInput') refInput: ElementRef;

  isPayment = false;
  referenceLength$: Observable<number>;

  constructor() {}

  ngOnInit(): void {
    this.isPayment = this.type === 'payment';

    this.referenceLength$ = this.setupReferenceLength();
  }

  onPasteQuoteRef(event: ClipboardEvent): void {
    const clipboardData = event.clipboardData;
    const clippedText = clipboardData.getData('text').substring(0, 35);
    const sanitizedText = this.sanitizeInput(clippedText);

    this.onPaste.emit(sanitizedText);
  }

  sanitizeInput(input) {
    // First, replace newline characters that occur between two words with a space
    let sanitizedInput = input.replace(/(\w)(\r\n|\n|\r)(\w)/gm, '$1 $3');

    // Then, remove any remaining newline characters that might be trailing or between paragraphs
    sanitizedInput = sanitizedInput.replace(/(\r\n|\n|\r)/gm, '');

    return sanitizedInput;
  }

  setupReferenceLength(): Observable<number> {
    // setupReference is added inside the ngOnInit method beacuse of this.control.valueChanges observable
    return this.control.valueChanges.pipe(
      startWith(this.control.value),
      map((value: string) => this.getReferenceLength(value)),
    );
  }

  private getReferenceLength(value: string): number {
    const length = value ? value.length : 0;
    return length < this.maxLength ? length : this.maxLength;
  }
}
