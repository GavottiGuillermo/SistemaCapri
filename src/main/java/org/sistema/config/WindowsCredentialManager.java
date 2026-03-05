package org.sistema.config;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Acceso simplificado al Administrador de credenciales de Windows sin depender de jna-platform.
 */
public final class WindowsCredentialManager {

    private static final int CRED_TYPE_GENERIC = 1;
    private static final Set<Charset> CANDIDATE_ENCODINGS = new LinkedHashSet<>(Arrays.asList(
            StandardCharsets.UTF_16LE,
            StandardCharsets.UTF_8,
            Charset.defaultCharset()
    ));

    private WindowsCredentialManager() {
    }

    public static Optional<String> readPassword(String target) {
        if (target == null || target.isBlank() || !Platform.isWindows()) {
            return Optional.empty();
        }

        PointerByReference credentialRef = new PointerByReference();
        Pointer credentialPointer = null;
        try {
            Advapi32 advapi32 = Advapi32.INSTANCE;
            boolean readOk = advapi32.CredReadW(new WString(target), CRED_TYPE_GENERIC, 0, credentialRef);
            if (!readOk) {
                return Optional.empty();
            }
            credentialPointer = credentialRef.getValue();
            if (credentialPointer == null) {
                return Optional.empty();
            }

            WinCredential credential = new WinCredential(credentialPointer);
            credential.read();
            String secret = credential.extractSecret();
            if (secret == null || secret.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(secret);
        } catch (UnsatisfiedLinkError | NoClassDefFoundError ex) {
            return Optional.empty();
        } finally {
            if (credentialPointer != null) {
                Advapi32.INSTANCE.CredFree(credentialPointer);
            }
        }
    }

    private interface Advapi32 extends StdCallLibrary {
        Advapi32 INSTANCE = Native.load("Advapi32", Advapi32.class, W32APIOptions.UNICODE_OPTIONS);

        boolean CredReadW(WString targetName, int type, int reservedFlag, PointerByReference credential);

        void CredFree(Pointer credential);
    }

    @SuppressWarnings("unused")
    private static final class WinCredential extends Structure {
        public int Flags;
        public int Type;
        public WString TargetName;
        public WString Comment;
        public FileTime LastWritten;
        public int CredentialBlobSize;
        public Pointer CredentialBlob;
        public int Persist;
        public int AttributeCount;
        public Pointer Attributes;
        public WString TargetAlias;
        public WString UserName;

        WinCredential(Pointer pointer) {
            super(pointer);
        }

        @Override
        protected List<String> getFieldOrder() {
            return List.of(
                    "Flags",
                    "Type",
                    "TargetName",
                    "Comment",
                    "LastWritten",
                    "CredentialBlobSize",
                    "CredentialBlob",
                    "Persist",
                    "AttributeCount",
                    "Attributes",
                    "TargetAlias",
                    "UserName"
            );
        }

        String extractSecret() {
            if (CredentialBlob == null || CredentialBlobSize <= 0) {
                return null;
            }
            byte[] raw = CredentialBlob.getByteArray(0, CredentialBlobSize);
            for (Charset charset : CANDIDATE_ENCODINGS) {
                String candidate = new String(raw, charset).replace("\u0000", "");
                if (!candidate.isBlank()) {
                    return candidate;
                }
            }
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static final class FileTime extends Structure {
        public int dwLowDateTime;
        public int dwHighDateTime;

        @Override
        protected List<String> getFieldOrder() {
            return List.of("dwLowDateTime", "dwHighDateTime");
        }
    }
}
