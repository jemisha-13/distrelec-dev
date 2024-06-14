import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageCms3Component } from './page-cms3.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { ParseHtmlPipeModule } from 'src/app/spartacus/pipes/parse-html-pipe.module';
import { PageCms3LayoutModule } from './page-cms3-layout';

@NgModule({
  declarations: [PageCms3Component],
  imports: [CommonModule, PageCms3LayoutModule, FontAwesomeModule, RouterModule, ParseHtmlPipeModule],
  exports: [PageCms3Component],
})
export class PageCms3Module {}
