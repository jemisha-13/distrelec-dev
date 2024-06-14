export interface AddressChangeFormInt {
  customerNumber: string;
  oldCompanyName: string;
  oldFirstName: string;
  oldLastName: string;
  oldStreet: string;
  oldPostalCode: string;
  oldTown: string;
  oldCountry: string;
  oldDepartment?: string;
  oldNumber?: string;
  oldPlace?: string;
  newCompanyName: string;
  newFirstName: string;
  newLastName: string;
  newStreet: string;
  newPostalCode: string;
  newTown: string;
  newCountry: string;
  newDepartment?: string;
  newNumber?: string;
  newPlace?: string;
  comment?: string;
}

export interface AddressChangeRespInt {
  customerNumber: string;
  oldAddress: Address;
  newAddress: Address;
  comment?: string;
}
interface Address {
  companyName: string;
  firstName: string;
  lastName: string;
  street: string;
  town: string;
  country: string;
  zip: string;
  department?: string;
  number?: string;
  place?: string;
}
