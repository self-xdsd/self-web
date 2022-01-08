import {AbstractControl, ValidationErrors, ValidatorFn} from "@angular/forms";

/**
 * Global notBlank() validation function. It checks for empty or blank (only spaces) fields.
 */
export function notBlank(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const blank = control.value == null || control.value.trim().length == 0;
    return !blank ? null : {required: {value: control.value}};
  };
}
