package org.mycommon.modules.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.zip.*;

/**
 * author:KangXinghua
 * desc:字符串压缩/解压工具，提供gzip和zip两种方式，压缩后的字符串使用base64转码
 */

public class Zips {

    private final static Logger logger = LoggerFactory.getLogger(Zips.class);

    /**
     * 使用gzip进行压缩
     */
    public static String gzip(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes(Charset.forName("utf-8")));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new String(new Base64().encode(out.toByteArray()));
    }

    /**
     * 使用gzip进行解压缩
     *
     * @param compressedStr
     * @return 解压后的字符串
     */
    public static String gunzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = new Base64().decode(compressedStr.getBytes());
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString("utf-8");
        } catch (IOException e) {
            logger.error("压缩错误:", e);
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                    logger.error("压缩错误:", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("压缩错误:", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("压缩错误:", e);
                }
            }
        }

        return decompressed;
    }

    /**
     * 使用zip进行压缩
     *
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    public static final String zip(String str) {
        if (str == null)
            return null;
        byte[] compressed;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;
        String compressedStr = null;
        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes(Charset.forName("utf-8")));
            zout.closeEntry();
            compressed = out.toByteArray();
            compressedStr = new String(new Base64().encode(compressed));
        } catch (IOException e) {
            compressed = null;
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return compressedStr;
    }

    /**
     * 使用zip进行解压缩
     *
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static final String unzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        String decompressed = null;
        try {
            byte[] compressed = new Base64().decode(compressedStr.getBytes());
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString("utf-8");
        } catch (IOException e) {
            decompressed = null;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }

    /**
     * 压缩文件
     */
    public static ByteArrayOutputStream zipFiles(Map<String, ByteArrayInputStream> mapInputStream) {
        byte[] buf = new byte[1024];
        try {
            // Create the ZIP file
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ZipOutputStream out = new ZipOutputStream(os);
            // Compress the files
            for (Map.Entry<String, ByteArrayInputStream> entry : mapInputStream.entrySet()) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(entry.getKey()));
                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = entry.getValue().read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                // Complete the entry
                out.closeEntry();
                entry.getValue().close();
            }
            // Complete the ZIP file
            out.close();
            return os;
        } catch (IOException e) {
            logger.error("ZipUtil zipFiles exception:" + e);
            return null;
        }
    }
}

