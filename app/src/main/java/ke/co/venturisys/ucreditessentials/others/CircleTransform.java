package ke.co.venturisys.ucreditessentials.others;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

import static ke.co.venturisys.ucreditessentials.others.Constants.BORDER_COLOR;
import static ke.co.venturisys.ucreditessentials.others.Constants.BORDER_RADIUS;

public class CircleTransform implements Transformation {

    private boolean border;

    CircleTransform(boolean border) {
        this.border = border;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;

        if (border) {
            // Prepare the background
            Paint paintBg = new Paint();
            paintBg.setColor(BORDER_COLOR);
            paintBg.setAntiAlias(true);

            // draw the background circle
            canvas.drawCircle(r, r, r, paintBg);
            float radius = r - BORDER_RADIUS;


            // Draw the image smaller than the background so a little border will be seen
            canvas.drawCircle(r, r, radius, paint);
        } else {
            // only draw the image
            canvas.drawCircle(r, r, r, paint);
        }

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return getClass().getName();
    }
}
