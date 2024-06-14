/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { SeEntryModule } from 'smarteditcommons';

@SeEntryModule('distrelecsmartedit')
@NgModule({
    imports: [BrowserModule],
    providers: []
})
export class DistrelecsmarteditModule {}
