import { DistIcon } from '@model/icon.model';

export const progressSpinner: DistIcon = {
  name: 'progressSpinner',
  viewbox: '0 0 200 200',
  dimensions: [20, 21],
  colour: 'none',
  elements: [
    {
      type: 'element',
      tagName: 'defs',
      properties: {},
      children: [
        {
          type: 'element',
          tagName: 'linearGradient',
          properties: { id: 'spinner-secondHalf' },
          children: [
            {
              type: 'element',
              tagName: 'stop',
              properties: { offset: '0%', 'stop-opacity': 0, 'stop-color': 'currentColor' },
              children: [],
            },
            {
              type: 'element',
              tagName: 'stop',
              properties: { offset: '100%', 'stop-opacity': 0.5, 'stop-color': 'currentColor' },
              children: [],
            },
          ],
        },
        {
          type: 'element',
          tagName: 'linearGradient',
          properties: { id: 'spinner-firstHalf' },
          children: [
            {
              type: 'element',
              tagName: 'stop',
              properties: { offset: '0%', 'stop-opacity': 1, 'stop-color': 'currentColor' },
              children: [],
            },
            {
              type: 'element',
              tagName: 'stop',
              properties: { offset: '100%', 'stop-opacity': 0.5, 'stop-color': 'currentColor' },
              children: [],
            },
          ],
        },
      ],
    },
    {
      type: 'element',
      tagName: 'g',
      properties: { 'stroke-width': 16 },
      children: [
        {
          type: 'element',
          tagName: 'path',
          properties: { stroke: 'url(#spinner-secondHalf)', d: 'M 4 100 A 96 96 0 0 1 196 100' },
          children: [],
        },
        {
          type: 'element',
          tagName: 'path',
          properties: { stroke: 'url(#spinner-firstHalf)', d: 'M 196 100 A 96 96 0 0 1 4 100' },
          children: [],
        },
        {
          type: 'element',
          tagName: 'path',
          properties: { stroke: 'currentColor', 'stroke-linecap': 'round', d: 'M 4 100 A 96 96 0 0 1 4 98' },
          children: [],
        },
      ],
    },
  ],
};
