import { GlobalMessageConfig, GlobalMessageType } from '@spartacus/core';

export function distGlobalMessageConfigFactory(): GlobalMessageConfig {
  return {
    globalMessages: {
      [GlobalMessageType.MSG_TYPE_CONFIRMATION]: {
        timeout: 5000,
      },
      [GlobalMessageType.MSG_TYPE_ERROR]: {
        timeout: undefined,
      },
    },
  };
}
