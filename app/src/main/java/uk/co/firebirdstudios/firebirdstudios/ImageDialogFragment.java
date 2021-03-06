package uk.co.firebirdstudios.firebirdstudios;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;


public class ImageDialogFragment extends DialogFragment {
    /*
    This is where the Images are loaded in the Dialog box when the user touches a equipment item
    in the equipment hire tab
     */
    public ImageDialogFragment newInstance(int resID) {
        ImageDialogFragment imageDialogFragment = new ImageDialogFragment();
        /*

         */
        Bundle args = new Bundle();
        args.putInt("resID", resID);
        imageDialogFragment.setArguments(args);

        return imageDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int resID = getArguments().getInt("resID");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_equipment_image, null);
        ImageView equipmentImage = (ImageView) v.findViewById(R.id.equipment_image);
        TextView equipmentTitle = (TextView)v.findViewById(R.id.equipment_title);
        if (resID == R.id.bass_amp) {
            loadBitmap(R.drawable.bx3000t, equipmentImage);
            equipmentTitle.setText(R.string.bass_amp);
        } else if (resID == R.id.guitar_amp_standard) {
            loadBitmap(R.drawable.standard_amp, equipmentImage);
            equipmentTitle.setText(R.string.standard_guitar_amp);
        } else if (resID == R.id.guitar_amp_premium) {
            loadBitmap(R.drawable.premium_amp, equipmentImage);
            equipmentTitle.setText(R.string.premium_guitar_amp);
        } else if (resID == R.id.cymbals) {
            loadBitmap(R.drawable.cymbals, equipmentImage);
            equipmentTitle.setText(R.string.cymbals);
        } else if (resID == R.id.snare) {
            loadBitmap(R.drawable.snare, equipmentImage);
            equipmentTitle.setText(R.string.snare);
        } else if (resID == R.id.drum_kit) {
            loadBitmap(R.drawable.drum_kit, equipmentImage);
            equipmentTitle.setText(R.string.drum_kit);
        }

        builder.setView(v);
        Dialog dialog = builder.create();

        return dialog;
    }
    /*
    this code was created by following the Android tutorial in how to load bitmaps
    Tutorial is found at
    http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
     */
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageview) {
            imageViewReference = new WeakReference<>(imageview);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeBitmapFromResource(getResources(), data, 100, 100);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public void loadBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }
}
