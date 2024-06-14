import { BehaviorSubject } from 'rxjs';
import { User } from '@spartacus/core';

const MOCK_IDENTIFY_USER_DATA: Partial<User> = {
  type: 'customer',
  uid: 'test@test.com',
  contactId: '544545',
};

// eslint-disable-next-line @typescript-eslint/naming-convention
export const MockIdentifyUserService = {
  userDetails_: new BehaviorSubject<any>(MOCK_IDENTIFY_USER_DATA),

  getUserDetails(): BehaviorSubject<any> {
    return this.userDetails_;
  },
};
