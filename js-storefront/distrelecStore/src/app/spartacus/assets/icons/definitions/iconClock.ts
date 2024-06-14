import { DistIcon } from '@model/icon.model';

export const iconClock: DistIcon = {
  name: 'iconClock',
  viewbox: '0 0 20 20',
  dimensions: [20, 20],
  colour: 'none',
  elements: [
    {
      type: 'element',
      tagName: 'g',
      properties: { id: 'Icon' },
      children: [
        {
          type: 'element',
          tagName: 'path',
          properties: {
            id: 'iconClock',
            d: 'M10 1.66666C5.41669 1.66666 1.66669 5.41666 1.66669 10C1.66669 14.5833 5.41669 18.3333 10 18.3333C14.5834 18.3333 18.3334 14.5833 18.3334 10C18.3334 5.41666 14.5834 1.66666 10 1.66666ZM12.9584 13.1667L9.55835 11.075C9.30835 10.925 9.15835 10.6583 9.15835 10.3667V6.45833C9.16669 6.11666 9.45002 5.83333 9.79169 5.83333C10.1334 5.83333 10.4167 6.11666 10.4167 6.45833V10.1667L13.6167 12.0917C13.9167 12.275 14.0167 12.6667 13.8334 12.9667C13.65 13.2583 13.2584 13.35 12.9584 13.1667Z',
            fill: '#393E41',
          },
          children: [],
        },
      ],
    },
  ],
};
