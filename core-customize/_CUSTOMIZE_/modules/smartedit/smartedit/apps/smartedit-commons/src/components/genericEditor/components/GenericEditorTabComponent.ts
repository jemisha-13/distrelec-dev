/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

/* tslint:disable */

import {
    AfterViewChecked,
    ChangeDetectorRef,
    Component,
    Inject,
    OnDestroy,
    ViewRef
} from '@angular/core';
import { AbstractForm, AbstractForms, FormGrouping } from '@smart/utils';
import { values } from 'lodash';
import { Subscription } from 'rxjs';

import { TabData, TAB_DATA } from '../../tabs/TabComponent';
import { AbstractGenericEditorComponent } from '../AbstractGenericEditorComponent';
import { GenericEditorField } from '../types';

import { IExperience } from '../../../services/interfaces/IExperience';
import { IExperienceService } from '../../../services/interfaces/IExperienceService';
import { SystemEventService } from '../../../services/SystemEventService';

import { EVENTS } from '../../../utils';

@Component({
    selector: 'se-ge-tab',
    templateUrl: './GenericEditorTabComponent.html'
})
export class GenericEditorTabComponent implements OnDestroy, AfterViewChecked {
    public forms: AbstractForms;

    public fields: GenericEditorField[];
    private _subscription: Subscription;
    public hideComponentFlag: boolean = true;
    public experience: IExperience;
    private unregFn: () => void;

    constructor(
        private readonly cdr: ChangeDetectorRef,
        public ge: AbstractGenericEditorComponent,
        private systemEventService: SystemEventService,
        private experienceService: IExperienceService,
        @Inject(TAB_DATA) private data: TabData<FormGrouping>
    ) {}

    ngOnInit(): void {
        const { model: master, tabId } = this.data;
        this.fields = master.getInput('fieldsMap')[tabId];
        this.hideComponentFlag = true;

        this.updateExperience();
        this.unregFn = this.systemEventService.subscribe(EVENTS.EXPERIENCE_UPDATE, () =>
            this.updateExperience()
        );

        const group = master.controls[tabId] as FormGrouping;
        this.forms = group.controls;

        this._subscription = group.statusChanges.subscribe(() => {
            const hasErrorMessages = values(this.forms).some((form) => {
                const field = form.getInput('field') as GenericEditorField;
                return field.messages && field.messages.length > 0;
            });

            this.data.tab.hasErrors = hasErrorMessages;
        });

        Object.keys(this.forms).forEach((key) => {
            this.forms[key].setInput('id', this.ge.editor.id);
        });

    }

    ngAfterViewChecked(): void {
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    ngOnDestroy(): void {
        this._subscription.unsubscribe();
        this.unregFn();
    }

    public async updateExperience(): Promise<void> {
        this.experience = await this.experienceService.getCurrentExperience();
    }

     public isInternational(id: string) {
        let idCollectionToHide = ['displayOnSites','displayStatus','robotTag'];

        // wait till promise is resolved as variable will be undefined
         if(typeof this.experience !== "undefined") {
             const getContentCatalogName = this.experience.catalogDescriptor.catalogId;

             if (getContentCatalogName !== "distrelec-IntContentCatalog" && idCollectionToHide.indexOf(id) !== -1) {
                 return !this.hideComponentFlag;
             }
         }

        return this.hideComponentFlag;
    }

    public isAll(id: string) {
        let idCollectionToHide = ['styleClasses'];

        if(idCollectionToHide.indexOf(id) !== -1) {
            return !this.hideComponentFlag;
        }

        return this.hideComponentFlag;
    }

    getForm(id: string): AbstractForm {
        return this.forms[id];
    }
}
