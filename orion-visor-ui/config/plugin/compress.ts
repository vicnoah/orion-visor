import type { Plugin } from 'vite';
import compressPlugin from 'vite-plugin-compression';

/**
 * gzip 压缩
 * https://github.com/anncwb/vite-plugin-compression
 */
export default function configCompressPlugin(
  compress: 'gzip' | 'brotli',
  deleteOriginFile = false
): Plugin | Plugin[] {
  const plugins: Plugin[] = [];

  if (compress === 'gzip') {
    plugins.push(
      compressPlugin({
        ext: '.gz',
        deleteOriginFile,
      })
    );
  }
  // br 压缩
  if (compress === 'brotli') {
    plugins.push(
      compressPlugin({
        ext: '.br',
        algorithm: 'brotliCompress',
        deleteOriginFile,
      })
    );
  }
  return plugins;
}
