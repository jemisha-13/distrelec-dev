/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, DoCheck, Type } from '@angular/core';
import {
    DynamicForm,
    DynamicInput,
    DynamicInputChange,
    FormGrouping,
    ISessionService,
    Payload,
    TypedMap,
    ILanguage
} from '@smart/utils';

import { VALIDATION_MESSAGE_TYPES } from '../../../utils/smarteditconstants';
import { Tab } from '../../tabs/types';
import { GenericEditorField, GenericEditorFieldMessage } from '../types';
import { GenericEditorFieldWrapperComponent } from './GenericEditorFieldWrapperComponent';
import { isEqual } from 'lodash';

@Component({
    selector: 'se-localized-element',
    styles: [
        `
            :host {
                display: block;
            }
        `
    ],
    templateUrl: './LocalizedElementComponent.html'
})
export class LocalizedElementComponent implements DoCheck, DynamicInputChange {
    @DynamicForm()
    form: FormGrouping;

    @DynamicInput()
    field: GenericEditorField;

    @DynamicInput()
    component: Payload;

    @DynamicInput()
    languages: ILanguage[];

    tabs: Tab[];
    private _previousMessages: GenericEditorFieldMessage[];

    constructor(private sessionService: ISessionService) {}

    onDynamicInputChange(): void {
        this._createLocalizedTabs();
    }

    /**
     * TODO: Could probably remove this method after replacing native HTML, validation with
     * Angular validation.
     */
    ngDoCheck(): void {
        this.updateTabDisplay.call(this, this.tabs);

        if (this.tabs && this.field.messages !== this._previousMessages) {
            this._previousMessages = this.field.messages;

            const messageMap: TypedMap<boolean> = this.field.messages
                ? this.field.messages
                      .filter(
                          (message: GenericEditorFieldMessage) =>
                              message.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR
                      )
                      .reduce((holder: TypedMap<boolean>, next) => {
                          holder[next.language] = true;
                          return holder;
                      }, {})
                : {};
            this.tabs = this.tabs.map(function (tab) {
                const message = messageMap[tab.id];
                tab.hasErrors = message !== undefined ? message : false;
                return tab;
            });
        }
    }

    /**
     * Map fields to localized tabs.
     *
     * @returns {Promise<void>}
     * @private
     */
    private async _createLocalizedTabs(): Promise<void> {
        this.field.isLanguageEnabledMap = {};
        const { readableLanguages, writeableLanguages } =
            await this.sessionService.getCurrentUser();

        const readableSet = new Set(readableLanguages);
        const writeable = new Set(writeableLanguages);

        this.tabs = this.languages
            .filter((language: ILanguage) => readableSet.has(language.isocode))
            .map(({ isocode, required }: ILanguage) => {
                this.field.isLanguageEnabledMap[isocode] = writeable.has(isocode);

                const title = `${isocode.toUpperCase()}${
                    this.field.editable && this.field.required && required ? '*' : ''
                }`;
                return {
                    title,
                    hasErrors: false,
                    id: isocode,
                    component: GenericEditorFieldWrapperComponent
                };
            });

        this.sortLocalizedTabs();
    }

    private sortLocalizedTabs() {
        this.updateTabDisplay.call(this, this.tabs);

        if (this.tabs) {
            const priorityLanguages = this.languages
                .filter((language: ILanguage) => {
                    return language.rank && language.rank > 0;
                })                
                .sort((langA: ILanguage, langB: ILanguage) => {
                    //@ts-ignore
                    return +langB.rank - +langA.rank;
                })
                .map((language: ILanguage) => {
                    return language.isocode;
                });

            const tabsWithContent = this.tabs
                .filter((tab: Tab) => {
                    return tab.hasContent;
                });

            const prioritizedTabsWithContent = tabsWithContent
                .filter((tab: Tab) => {
                    return priorityLanguages.indexOf(tab.id) !== -1;
                })
                .sort((tab1: Tab, tab2: Tab) => {
                    const lang1Idx = priorityLanguages.indexOf(tab1.id);
                    const lang2Idx = priorityLanguages.indexOf(tab2.id);
                    return lang1Idx - lang2Idx;
                });

            const nonPrioritizedTabsWithContent = tabsWithContent
                .filter((tab: Tab) => {
                    return priorityLanguages.indexOf(tab.id) === -1;
                });

            const tabsWithoutContent = this.tabs
                .filter((tab: Tab) => {
                    return !tab.hasContent;
                });

            const prioritizedTabsWithoutContent = tabsWithoutContent
                .filter((tab: Tab) => {
                    return priorityLanguages.indexOf(tab.id) !== -1;
                })
                .sort((tab1: Tab, tab2: Tab) => {
                    const lang1Idx = priorityLanguages.indexOf(tab1.id);
                    const lang2Idx = priorityLanguages.indexOf(tab2.id);
                    return lang1Idx - lang2Idx;
                });

            const nonPrioritizedTabsWithoutContent = tabsWithoutContent
                .filter((tab: Tab) => {
                    return priorityLanguages.indexOf(tab.id) === -1;
                });

            const orderedTabs = prioritizedTabsWithContent.concat(nonPrioritizedTabsWithContent)
                .concat(prioritizedTabsWithoutContent)
                .concat(nonPrioritizedTabsWithoutContent);
            if (!isEqual(this.tabs, orderedTabs)) {
                this.tabs = orderedTabs;
            }
        }
    }

    private updateTabDisplay(tabs: Tab[]) {
        if (tabs) {
            tabs.forEach(function(tab: Tab) {
                const languageCode = tab.id;
                const content = this.component[this.field.qualifier][languageCode];
                if (content) {
                    tab.hasContent = true;
                } else {
                    tab.hasContent = false;
                }
            }.bind(this));
        }
    }
}
