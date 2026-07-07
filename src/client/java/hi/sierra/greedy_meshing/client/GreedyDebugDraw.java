package hi.sierra.greedy_meshing.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.loader.api.FabricLoader;
import org.joml.Matrix4f;

/**
 * Shared geometry + style selection for the greedy-mesh debug overlay.
 *
 * <p>The overlay is one backend-agnostic system (vanilla/Indigo, Sodium and VulkanMod all feed
 * {@link GreedyDebugStore}; {@link GreedyWireframeRenderer} and {@code DebugRendererMixin} both draw it).
 * It renders as crisp line OUTLINES — except under VulkanMod &lt; 0.6, whose {@code rendertype_lines}
 * shader explodes lines into giant triangles on MoltenVK. There we fall back to translucent {@code
 * debugQuads} FILLS, which VulkanMod renders correctly. {@link #useFills()} picks per-launch from the
 * loaded VulkanMod version (runtime, not build-time: the same jar may run on Sodium where lines are fine).
 *
 * <p>Callers pick the matching buffer (lines vs {@code debugQuads}) from {@code useFills()} and then call
 * {@link #outline}/{@link #grid}/{@link #triangles}, which emit lines or fills to match. Fills are lifted
 * slightly toward the camera to avoid z-fighting the coplanar face (imperceptible on a full-surface fill);
 * lines are left unlifted (a lifted thin line looks detached).
 */
public final class GreedyDebugDraw {
    /** In-plane inset (blocks) so adjacent fills show a gap instead of merging into one blob. */
    private static final float INSET = 0.03f;
    /** Depth lift (blocks) toward the camera so a coplanar fill wins the depth test (anti z-fight). */
    private static final float CAMERA_LIFT = 0.02f;

    private static double camX;
    private static double camY;
    private static double camZ;
    private static Boolean useFills;

    private GreedyDebugDraw() {
    }

    /** True when the active renderer mangles lines (VulkanMod &lt; 0.6) → use filled quads instead. */
    public static boolean useFills() {
        if (useFills == null) {
            useFills = FabricLoader.getInstance().getModContainer("vulkanmod").map(c -> {
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\.(\\d+)")
                        .matcher(c.getMetadata().getVersion().getFriendlyString());
                // VulkanMod 0.4.x/0.5.x bundle the broken line shader; 0.6.x+ render lines fine.
                return m.find() && Integer.parseInt(m.group(1)) == 0 && Integer.parseInt(m.group(2)) < 6;
            }).orElse(false);
        }
        return useFills;
    }

    /** Stash the camera position so fills can be lifted toward it (see {@link #CAMERA_LIFT}). */
    public static void setCamera(double x, double y, double z) {
        camX = x;
        camY = y;
        camZ = z;
    }

    // ------------------------------------------------------------------
    // Per-quad entry points: emit lines or fills depending on `fills`.
    // ------------------------------------------------------------------

    /** Greedy run: one filled quad, or its 4-edge outline. */
    public static void outline(VertexConsumer c, Matrix4f pose, GreedyDebugStore.DebugQuad q,
                               boolean fills, float a, float r, float g, float b) {
        if (fills) {
            drawFilledQuad(c, pose, q.x0(), q.y0(), q.z0(), q.x1(), q.y1(), q.z1(),
                    q.x2(), q.y2(), q.z2(), q.x3(), q.y3(), q.z3(), a, r, g, b);
        } else {
            drawEdge(c, pose, q.x0(), q.y0(), q.z0(), q.x1(), q.y1(), q.z1(), a, r, g, b);
            drawEdge(c, pose, q.x1(), q.y1(), q.z1(), q.x2(), q.y2(), q.z2(), a, r, g, b);
            drawEdge(c, pose, q.x2(), q.y2(), q.z2(), q.x3(), q.y3(), q.z3(), a, r, g, b);
            drawEdge(c, pose, q.x3(), q.y3(), q.z3(), q.x0(), q.y0(), q.z0(), a, r, g, b);
        }
    }

    /** Vanilla comparison side: per-block cells (fills) or the per-block grid (lines). */
    public static void grid(VertexConsumer c, Matrix4f pose, GreedyDebugStore.DebugQuad q,
                            float ex, float ey, float ez, float fx, float fy, float fz,
                            int w, int h, boolean fills, float a, float r, float g, float b) {
        if (fills) {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    float au = (float) i / w, bu = (float) (i + 1) / w;
                    float av = (float) j / h, bv = (float) (j + 1) / h;
                    drawFilledQuad(c, pose,
                            q.x0() + ex * au + fx * av, q.y0() + ey * au + fy * av, q.z0() + ez * au + fz * av,
                            q.x0() + ex * bu + fx * av, q.y0() + ey * bu + fy * av, q.z0() + ez * bu + fz * av,
                            q.x0() + ex * bu + fx * bv, q.y0() + ey * bu + fy * bv, q.z0() + ez * bu + fz * bv,
                            q.x0() + ex * au + fx * bv, q.y0() + ey * au + fy * bv, q.z0() + ez * au + fz * bv,
                            a, r, g, b);
                }
            }
        } else {
            drawEdge(c, pose, q.x0(), q.y0(), q.z0(), q.x1(), q.y1(), q.z1(), a, r, g, b);
            drawEdge(c, pose, q.x1(), q.y1(), q.z1(), q.x2(), q.y2(), q.z2(), a, r, g, b);
            drawEdge(c, pose, q.x2(), q.y2(), q.z2(), q.x3(), q.y3(), q.z3(), a, r, g, b);
            drawEdge(c, pose, q.x3(), q.y3(), q.z3(), q.x0(), q.y0(), q.z0(), a, r, g, b);
            for (int i = 1; i < w; i++) {
                float t = (float) i / w;
                drawEdge(c, pose, q.x0() + ex * t, q.y0() + ey * t, q.z0() + ez * t,
                        q.x3() + ex * t, q.y3() + ey * t, q.z3() + ez * t, a, r, g, b);
            }
            for (int j = 1; j < h; j++) {
                float t = (float) j / h;
                drawEdge(c, pose, q.x0() + fx * t, q.y0() + fy * t, q.z0() + fz * t,
                        q.x1() + fx * t, q.y1() + fy * t, q.z1() + fz * t, a, r, g, b);
            }
        }
    }

    /** Triangle-count indicator: a faint fill, or the quad's split diagonal as a line. */
    public static void triangles(VertexConsumer c, Matrix4f pose, GreedyDebugStore.DebugQuad q,
                                 boolean fills, float a, float r, float g, float b) {
        if (fills) {
            drawFilledQuad(c, pose, q.x0(), q.y0(), q.z0(), q.x1(), q.y1(), q.z1(),
                    q.x2(), q.y2(), q.z2(), q.x3(), q.y3(), q.z3(), a, r, g, b);
        } else {
            drawEdge(c, pose, q.x0(), q.y0(), q.z0(), q.x2(), q.y2(), q.z2(), a, r, g, b);
        }
    }

    // ------------------------------------------------------------------
    // Fill primitives (POSITION_COLOR / QUADS, camera-lifted, inset).
    // ------------------------------------------------------------------

    private static void drawFilledQuad(VertexConsumer fill, Matrix4f pose,
                                       float x0, float y0, float z0, float x1, float y1, float z1,
                                       float x2, float y2, float z2, float x3, float y3, float z3,
                                       float alpha, float red, float green, float blue) {
        if (alpha <= 0.0f) {
            return;
        }
        float ux = x1 - x0, uy = y1 - y0, uz = z1 - z0;
        float ul = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        if (ul > 1.0e-5f) { ux /= ul; uy /= ul; uz /= ul; }
        float vx = x3 - x0, vy = y3 - y0, vz = z3 - z0;
        float vl = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (vl > 1.0e-5f) { vx /= vl; vy /= vl; vz /= vl; }

        fillVertex(fill, pose, x0 + (ux + vx) * INSET, y0 + (uy + vy) * INSET, z0 + (uz + vz) * INSET, red, green, blue, alpha);
        fillVertex(fill, pose, x1 + (-ux + vx) * INSET, y1 + (-uy + vy) * INSET, z1 + (-uz + vz) * INSET, red, green, blue, alpha);
        fillVertex(fill, pose, x2 + (-ux - vx) * INSET, y2 + (-uy - vy) * INSET, z2 + (-uz - vz) * INSET, red, green, blue, alpha);
        fillVertex(fill, pose, x3 + (ux - vx) * INSET, y3 + (uy - vy) * INSET, z3 + (uz - vz) * INSET, red, green, blue, alpha);
    }

    /** One fill vertex, nudged toward the camera by {@link #CAMERA_LIFT} so it clears the face. */
    private static void fillVertex(VertexConsumer fill, Matrix4f pose, float x, float y, float z,
                                   float red, float green, float blue, float alpha) {
        double dx = camX - x, dy = camY - y, dz = camZ - z;
        double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (d > 1.0e-5) {
            float t = (float) (CAMERA_LIFT / d);
            x += (float) (dx * t);
            y += (float) (dy * t);
            z += (float) (dz * t);
        }
        fill.addVertex(pose, x, y, z).setColor(red, green, blue, alpha);
    }

    // ------------------------------------------------------------------
    // Line primitive (POSITION_COLOR_NORMAL). No camera lift — a lifted thin line looks detached.
    // ------------------------------------------------------------------

    private static void drawEdge(VertexConsumer lines, Matrix4f pose,
                                 float ax, float ay, float az, float bx, float by, float bz,
                                 float alpha, float red, float green, float blue) {
        if (alpha <= 0.0f) {
            return;
        }
        float nx = bx - ax, ny = by - ay, nz = bz - az;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 1.0e-5f) { nx /= len; ny /= len; nz /= len; }

        //? if >=1.21.11 {
        /*lines.addVertex(pose, ax, ay, az).setColor(red, green, blue, alpha).setNormal(nx, ny, nz).setLineWidth(1.0f);
        lines.addVertex(pose, bx, by, bz).setColor(red, green, blue, alpha).setNormal(nx, ny, nz).setLineWidth(1.0f);
        *///?} else {
        lines.addVertex(pose, ax, ay, az).setColor(red, green, blue, alpha).setNormal(nx, ny, nz);
        lines.addVertex(pose, bx, by, bz).setColor(red, green, blue, alpha).setNormal(nx, ny, nz);
        //?}
    }
}
