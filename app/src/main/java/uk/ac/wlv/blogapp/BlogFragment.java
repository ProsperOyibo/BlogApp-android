package uk.ac.wlv.blogapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class BlogFragment extends Fragment {
    private static final String ARG_BLOG_ID = "blog_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private Blog mBlog;
    private EditText mTitleField;
    private EditText mBlogField;
    Button mDateButton;
    Button mDeleteButton;
    CheckBox mDoneCheckBox;
    private Button mShareButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    public static BlogFragment newInstance (UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BLOG_ID, crimeId);
        BlogFragment fragment = new BlogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        UUID blogId = (UUID) getArguments().getSerializable(ARG_BLOG_ID);
        mBlog = BlogPost.get(getActivity()).getBlog(blogId);
        mPhotoFile = BlogPost.get(getActivity()).getPhotoFile(mBlog);
    }

    @Override
    public void onPause() {
        super.onPause();
        BlogPost.get(getActivity()).updateBlog(mBlog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blog, container, false);
        mTitleField = v.findViewById(R.id.blog_title);
        mTitleField.setText(mBlog.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBlog.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBlogField = v.findViewById(R.id.blog_details);
        mBlogField.setText(mBlog.getDetails());
        mBlogField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBlog.setDetails(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mBlogField.setScroller(new Scroller(getActivity()));
        mBlogField.setMaxLines(5);
        mBlogField.setVerticalScrollBarEnabled(true);
        mBlogField.setMovementMethod(new ScrollingMovementMethod());

        mDateButton = v.findViewById(R.id.blog_date);
        String pattern = "dd MMM, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(mBlog.getDate());
        mDateButton.setText(date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                CalendarFragment dialog = CalendarFragment.newInstance(mBlog.getDate());
                dialog.setTargetFragment(BlogFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mDoneCheckBox = v.findViewById(R.id.blog_complete);
        mDoneCheckBox.setChecked(mBlog.isComplete());
        mDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBlog.setComplete(isChecked);
            }
        });
        mDeleteButton = v.findViewById(R.id.blog_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlogPost.get(getActivity()).deleteBlog(mBlog);
                String deleteMessage = mBlog.getTitle() + " is deleted!";
                Toast.makeText(getActivity(), deleteMessage, Toast.LENGTH_SHORT).show();
            }
        });
        mShareButton = v.findViewById(R.id.blog_share);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                Uri photoUri= FileProvider.getUriForFile(getActivity(),
                        "uk.ac.wlv.blogapp.fileprovider", mPhotoFile);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getBlogPost());
                i.putExtra(Intent.EXTRA_STREAM, photoUri);
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.blog_subject));
                i = Intent.createChooser(i, getString(R.string.send_blog));
                startActivity(i);
            }
        });

        mPhotoButton = v.findViewById(R.id.blog_camera);
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "uk.ac.wlv.blogapp.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = v.findViewById(R.id.blog_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picture = new Intent(Intent.ACTION_VIEW);
                Uri photoUri= FileProvider.getUriForFile(getActivity(),
                        "uk.ac.wlv.blogapp.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                picture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                picture.setDataAndType(photoUri, "image/*");
                startActivity(picture);
            }
        });
        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(CalendarFragment.EXTRA_DATE);
            mBlog.setDate(date);
            String pattern = "dd MMM, yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String dateDisplay = simpleDateFormat.format(mBlog.getDate());
            mDateButton.setText(dateDisplay);
        }

        else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "uk.ac.wlv.blogapp.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private String getBlogPost() {
        String finishedString = null;
        if (mBlog.isComplete()) {
            finishedString = getString(R.string.blog_post_finished);
        } else {
            finishedString = getString(R.string.blog_post_unfinished);
        }
        String post = getString(R.string.blog_post,
                mBlog.getTitle(), mBlog.getDetails(), finishedString);
        return post;
    }


    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
