DOMAIN_NAME=workspace
MODEL_NAME=openrouter/openai/o4-mini-high
ATTACK_NAME=important_instructions
uv run bench \
  -s $DOMAIN_NAME \
  --attack $ATTACK_NAME \
  --model $MODEL_NAME --use-tacit
