/**
 * Format a given number as EUR.
 * @param value Number to format as EUR.
 */
export function formatEur(value: number): string {
  return new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' }).format(value);
}
