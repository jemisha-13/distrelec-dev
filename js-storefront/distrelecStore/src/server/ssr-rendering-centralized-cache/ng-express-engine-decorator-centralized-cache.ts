import {
  defaultSsrOptimizationOptions,
  getServerRequestProviders,
  NgSetupOptions,
  SsrOptimizationOptions,
} from '@spartacus/setup/ssr';
import { OptimizedSsrEngineCentralizedCache } from './optimized-ssr-engine-centralized-cache';
import { NgExpressEngine } from '@spartacus/setup/ssr/engine-decorator/ng-express-engine-decorator';
import { SsrOptimizationOptionsCentralizedCache } from './ssr-optimization-options-centralized-cache';

/**
 * The wrapper over the standard ngExpressEngine, that provides tokens for Spartacus
 *
 * @param ngExpressEngine
 */
export class NgExpressEngineDecoratorCentralizedCache {
  /**
   * Returns the higher order ngExpressEngine with provided tokens for Spartacus
   *
   * @param ngExpressEngine
   */
  static get(ngExpressEngine: NgExpressEngine, optimizationOptions?: SsrOptimizationOptions | null): NgExpressEngine {
    return decorateExpressEngineForCentralizedCache(ngExpressEngine, optimizationOptions);
  }
}

export function decorateExpressEngineForCentralizedCache(
  ngExpressEngine: NgExpressEngine,
  optimizationOptions: SsrOptimizationOptions | null | undefined = defaultSsrOptimizationOptions,
): NgExpressEngine {
  return (setupOptions: NgSetupOptions) => {
    const engineInstance = ngExpressEngine({
      ...setupOptions,
      providers: [
        // add spartacus related providers
        ...getServerRequestProviders(),
        ...(setupOptions.providers ?? []),
      ],
    });
    // apply optimization wrapper if optimization options were defined
    return optimizationOptions
      ? new OptimizedSsrEngineCentralizedCache(
          engineInstance,
          optimizationOptions as SsrOptimizationOptionsCentralizedCache,
        ).engineInstance
      : engineInstance;
  };
}
