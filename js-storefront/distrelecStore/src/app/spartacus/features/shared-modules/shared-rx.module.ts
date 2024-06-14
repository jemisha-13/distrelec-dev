import { ForModule } from '@rx-angular/template/for';
import { IfModule } from '@rx-angular/template/if';
import { LetModule } from '@rx-angular/template/let';
import { NgModule } from '@angular/core';

@NgModule({
  exports: [ForModule, IfModule, LetModule],
})
export class SharedRxModule {}
