import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ChannelService } from 'src/app/spartacus/site-context/services/channel.service';
import { Subscription } from 'rxjs';
import { Channel } from '@model/site-settings.model';
import { ICustomProduct } from '@model/product.model';

@Component({
  selector: 'app-scaled-prices',
  templateUrl: './scaled-prices.component.html',
  styleUrls: ['./scaled-prices.component.scss'],
})
export class ScaledPricesComponent implements OnInit, OnDestroy {
  @Input() product: ICustomProduct;

  channel: Channel;
  private subscriptions = new Subscription();

  constructor(private channelService: ChannelService) {}

  ngOnInit(): void {
    this.subscriptions.add(this.channelService.getActive().subscribe((channel) => (this.channel = channel)));
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}
