DOMAIN_NAME=banking
MODEL_NAME=openrouter/google/gemini-2.5-pro
ATTACK_NAME=important_instructions
uv run bench \
  -s $DOMAIN_NAME \
  --attack $ATTACK_NAME \
  --model $MODEL_NAME --use-tacit
