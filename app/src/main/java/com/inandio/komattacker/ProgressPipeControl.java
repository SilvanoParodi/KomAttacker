package com.inandio.komattacker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public final class ProgressPipeControl extends View
{

	private static final String TAG = ProgressPipeControl.class.getSimpleName();
	
	private Handler handler;

	// drawing tools
	private RectF rimRect;
	private Paint rimPaint;
	private Paint rimCirclePaint;
	
	private RectF faceRect;
	private Bitmap faceTexture;
	private Paint facePaint;
	private Paint rimShadowPaint;

    private RectF[] linesRect;
    private Paint linePaint;
	
	private RectF scaleRect;
	
	private Paint titlePaint;
    private Paint titlePaintLeft;
	private Path titlePath;

	private Paint logoPaint;
	private Bitmap logo;
	private Matrix logoMatrix;
	private float logoScale;
    private Paint verticalLinePaint;


	private Paint backgroundPaint;

    private RectF progressRect;
    private Paint progressPaint;
	
	
	// end drawing tools
	
	private Bitmap background; // holds the cached static part
	

	// hand dynamics -- all are angular expressed in F degrees

    private int _numberOfCheckpoints = 5;
    private float _totalDistanceMeter = 5000.0f;

    private float _currentDistance = 0.3f; //3000.0f;

    private float _parallelTrace = 0.0f; //3000.0f;

    private int _showReport = 0;
	
	
	public ProgressPipeControl(Context context)
    {
		super(context);
        setWillNotDraw(false);
		init();
	}

	public ProgressPipeControl(Context context, AttributeSet attrs)
    {
		super(context, attrs);
        setWillNotDraw(false);
		init();
	}

	public ProgressPipeControl(Context context, AttributeSet attrs, int defStyle)
    {
		super(context, attrs, defStyle);
        setWillNotDraw(false);
		init();
	}

    public void setNumberOfCheckpoints(int numberOfCheckpoints)
    {
        _numberOfCheckpoints = numberOfCheckpoints;
    }

    public void setTotalDistance(float distanceMeter)
    {
        _totalDistanceMeter = distanceMeter;
    }

    public void setHandTarget(float currentDistance)
    {

        if (currentDistance > 0.99f)
        {
            currentDistance = 0.99f;
        }
        _currentDistance = currentDistance;

        invalidate();
    }

    public void setCurrentDistance(float currentDistanceMeters)
    {
        float currentDistance = currentDistanceMeters/_totalDistanceMeter;
        setHandTarget(currentDistance);
    }

    public void setParallelTrace(float traceLen)
    {
        float realLen = traceLen/_totalDistanceMeter;


        if (realLen > 0.99f)
        {
            realLen = 0.99f;
        }
        _parallelTrace = realLen;

        invalidate();
    }

    public void showReport(int showReport)
    {
        _showReport = showReport;
        invalidate();
    }



	private void init()
    {
		handler = new Handler();
		initDrawingTools();
	}

	private void initDrawingTools()
    {

		rimRect = new RectF(0.1f, 0.0f, 0.5f, 1.0f);

		// the linear gradient is a bit skewed for realism
		rimPaint = new Paint();
		rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		rimPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f, 
										   Color.rgb(0xf0, 0xf5, 0xf0),
										   Color.rgb(0x30, 0x31, 0x30),
										   Shader.TileMode.CLAMP));		

		rimCirclePaint = new Paint();
		rimCirclePaint.setAntiAlias(true);
		rimCirclePaint.setStyle(Paint.Style.STROKE);
		rimCirclePaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));
        //rimCirclePaint.setStrokeWidth(0.005f);

		float rimSize = 0.02f;

        float lineSize = 0.02f;

		faceRect = new RectF();
		faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize, 
			     rimRect.right - rimSize, rimRect.bottom - rimSize);		

		faceTexture = BitmapFactory.decodeResource(getContext().getResources(), 
				   R.drawable.plastic);
		BitmapShader paperShader = new BitmapShader(faceTexture, 
												    Shader.TileMode.MIRROR, 
												    Shader.TileMode.MIRROR);

        facePaint = new Paint();
        facePaint.setAntiAlias(true);
        facePaint.setStyle(Paint.Style.FILL);
        facePaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f,
                Color.rgb(0xFF, 0x00, 0x00),
                Color.rgb(0xFF, 0x00, 0x00),
                Shader.TileMode.CLAMP));

        facePaint.setColor(Color.rgb(0xFF, 0xFF, 0x56));

		rimShadowPaint = new Paint();



        rimShadowPaint.setShader(paperShader);
		rimShadowPaint.setStyle(Paint.Style.FILL);




        // livelli (checkpoints)

        linesRect = new RectF[_numberOfCheckpoints];
        for(int i =0; i<_numberOfCheckpoints;i++ )
        {
            linesRect[i] = new RectF();

            float startLine = (1.0f/_numberOfCheckpoints) * i + rimSize;
            float endLine = startLine + lineSize;

            linesRect[i].set(rimRect.left + rimSize, startLine,
                    rimRect.right - rimSize, endLine);
        }

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f,
                Color.rgb(0xFF, 0x00, 0x00),
                Color.rgb(0xFF, 0x00, 0x00),
                Shader.TileMode.CLAMP));
        linePaint.setColor(Color.rgb(0xFF, 0x78, 0x56));


        progressRect = new RectF();
        progressRect.set(rimRect.left + rimSize, 0.9f,
                         rimRect.right - rimSize, 1.0f - rimSize);
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.FILL);

        progressPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f,
                Color.rgb(0x00, 0xFF, 0x00),
                Color.rgb(0x080, 0xFF, 0xF0),
                Shader.TileMode.CLAMP));



		float scalePosition = 0.10f;
		scaleRect = new RectF();
		scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
					  faceRect.right - scalePosition, faceRect.bottom - scalePosition);

		titlePaint = new Paint();
		titlePaint.setColor(0xaf946109);
		titlePaint.setAntiAlias(true);
		titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		//titlePaint.setTextAlign(Paint.Align.CENTER);
		titlePaint.setTextSize(0.05f);
		titlePaint.setTextScaleX(0.8f);

        titlePaintLeft = new Paint();
        titlePaintLeft.setColor(0xAF000000);
        titlePaintLeft.setAntiAlias(true);
        titlePaintLeft.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaintLeft.setTextSize(0.05f);
        //titlePaintLeft.setTextScaleX(0.8f);



		titlePath = new Path();
		//titlePath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -180.0f, -180.0f);
        titlePath.addRect(0.1f, 0.1f, 2.0f, 2.0f, Path.Direction.CW);

		logoPaint = new Paint();
		logoPaint.setFilterBitmap(true);
		logo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.logo);
		logoMatrix = new Matrix();
		logoScale = (1.0f / logo.getWidth()) * 0.4f;
		logoMatrix.setScale(logoScale, logoScale);

        verticalLinePaint = new Paint();
        verticalLinePaint.setAntiAlias(true);
        verticalLinePaint.setStyle(Paint.Style.FILL);
        verticalLinePaint.setColor(Color.rgb(0x00, 0x00, 0xFF));


        // TODO: test, dynamic checkpoints
        _checkPointTimes[0] = "-";
        _checkPointTimes[1] = "-";
        _checkPointTimes[2] = "-";
        _checkPointTimes[3] = "-";
        _checkPointTimes[4] = "-";
        _checkPointTimes[5] = "-";

        _checkPointDistances[0] = "";
        _checkPointDistances[1] = "";
        _checkPointDistances[2] = "";
        _checkPointDistances[3] = "";
        _checkPointDistances[4] = "";
        _checkPointDistances[5] = "";
		
		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
		//Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);
		
		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		
		setMeasuredDimension(chosenDimension, chosenDimension);
	}
	
	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		} 
	}




	
	// in case there is no size specified
	private int getPreferredSize()
    {
		return 800;
	}

	private void drawRim(Canvas canvas)
    {
		canvas.drawRect(rimRect, rimPaint);
		canvas.drawRect(rimRect, rimCirclePaint);
	}
	
	private void drawFace(Canvas canvas)
    {
		canvas.drawRect(faceRect, facePaint);
		// draw the inner rim circle
		canvas.drawRect(faceRect, rimCirclePaint);
		// draw the rim shadow inside the face
		canvas.drawRect(faceRect, rimShadowPaint);
	}

    private void drawLevels(Canvas canvas)
    {
        for(int i =0;i<_numberOfCheckpoints;i++)
        {
            canvas.drawRect(linesRect[i], linePaint);
        }
    }

    private void drawProgress(Canvas canvas, float value)
    {

        float rimSize = 0.02f;

        float valueNormalized = 1-value;

        progressRect.set(rimRect.left + rimSize, valueNormalized,
                rimRect.right - rimSize, 1.0f - rimSize);

        canvas.drawRect(progressRect, progressPaint);

    }


	private void drawTitle(Canvas canvas)
    {

        for(int i=0; i < _numberOfCheckpoints+1; i++)
        {
            float stepMeters = (float)_totalDistanceMeter/_numberOfCheckpoints * i;
            float stepKm = stepMeters/1000.0f;

            String text = String.valueOf(stepKm) + " Km" + " ("+ _checkPointDistances[i] + " Km/h)";
            float vPosition = 1.0f/_numberOfCheckpoints * i /*- 0.05f*/;
            vPosition = 1.0f - vPosition -0.05f;
            canvas.drawTextOnPath(text, titlePath, 0.45f, vPosition, titlePaint);

            if (_checkPointTimes[i]!= null)
                canvas.drawTextOnPath(_checkPointTimes[i], titlePath, 0.105f, vPosition, titlePaintLeft);
        }
/*
		canvas.drawTextOnPath("5 Km", titlePath, 0.45f,0.0f, titlePaint);

        canvas.drawTextOnPath("4 Km", titlePath, 0.45f,0.5f, titlePaint);
*/

        //_distanceMeter

        //canvas.drawText("title", 0.1f, 0.4f, titlePaint);
        //canvas.drawTextOnPath("Test", titlePath, 0.3f,0.0f, titlePaint);
    }

    private void drawVerticalLine(Canvas canvas, float length)
    {

        float len =  1.0f - length;
        canvas.drawLine(0.51f, 1.0f, 0.51f, len, verticalLinePaint);

    }


    private String[] _checkPointTimes = new String[_numberOfCheckpoints+1];
    private String[] _checkPointDistances = new String[_numberOfCheckpoints+1];
/*
    private void drawTitleLeft(Canvas canvas)
    {

        for(int i=0; i < _numberOfCheckpoints+1; i++)
        {

            String text =  "---";
            float vPosition = 1.0f/_numberOfCheckpoints * i ;
            vPosition = 1.0f - vPosition -0.05f;
            canvas.drawTextOnPath(text, titlePath, 0.1f, vPosition, titlePaint);
        }

    }
*/
	
	private void drawLogo(Canvas canvas)
    {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f - logo.getWidth() * logoScale / 2.0f, 
						 0.5f - logo.getHeight() * logoScale / 2.0f);

		int color = 0x00000000;
		float position = _currentDistance;
		if (position < 0) {
			color |= (int) ((0xf0) * -position); // blue
		} else {
			color |= ((int) ((0xf0) * position)) << 16; // red			
		}

        LightingColorFilter logoFilter = new LightingColorFilter(0xff338822, color);
		logoPaint.setColorFilter(logoFilter);
		
		canvas.drawBitmap(logo, logoMatrix, logoPaint);
		canvas.restore();		
	}

    private void drawReport(Canvas canvas, int showOption)
    {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(0.5f - logo.getWidth() * logoScale / 2.0f,
                0.5f - logo.getHeight() * logoScale / 2.0f);

        int color = 0x00000000;
        float position = _currentDistance;
        if (showOption == 1)
        {
            color = 0xFF0000;
        }
        else if (showOption == 2)
        {
            color = 0x00FF00;
        }
        else
        {
            //error
        }

        Paint reportPaint = new Paint();
        reportPaint.setFilterBitmap(true);
        Bitmap reportBmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.logo);
        logoMatrix = new Matrix();
        logoScale = (1.0f / logo.getWidth()) * 0.4f;
        logoMatrix.setScale(logoScale, logoScale);

        LightingColorFilter logoFilter = new LightingColorFilter(0xff338822, color);
        reportPaint.setColorFilter(logoFilter);

        canvas.drawBitmap(reportBmp, logoMatrix, reportPaint);
        canvas.restore();
    }


	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.w(TAG, "Background not created");
		} else {
			canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}

	}

    public void setCheckpointTimes(int index, String value)
    {
        _checkPointTimes[index] = value;
    }
    public void setCheckpointDistancesExtra(int index, String value)
    {
        _checkPointDistances[index] = value;
    }


    public void setDistance(Canvas canvas, float value)
    {
        float rimSize = 0.02f;
        progressRect.set(rimRect.left + rimSize, value, rimRect.right - rimSize, 1.0f - rimSize);
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(progressRect, progressPaint);
    }

	@Override
	protected void onDraw(Canvas canvas)
    {
		drawBackground(canvas);

        //drawTest(canvas);

        drawProgress(canvas, _currentDistance);

        regenerateBackground();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
		Log.d(TAG, "Size changed to " + w + "x" + h);
		
		regenerateBackground();
	}


	private void regenerateBackground()
    {
		if (background != null) {
			background.recycle();
		}
		
		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(background);
		float scale = (float) getWidth();		
		backgroundCanvas.scale(scale, scale);

		drawRim(backgroundCanvas);

		drawFace(backgroundCanvas);





        drawProgress(backgroundCanvas, _currentDistance);

        drawLevels(backgroundCanvas);

        //drawTitleLeft(backgroundCanvas);
        drawTitle(backgroundCanvas);

        drawVerticalLine(backgroundCanvas, _parallelTrace);

        if (_showReport != 0)
        {
            drawReport(backgroundCanvas, _showReport);
        }

	}


	





}
